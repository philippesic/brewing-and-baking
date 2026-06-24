// D1 migration required before deploying:
// CREATE TABLE IF NOT EXISTS rate_limits (
//   key TEXT PRIMARY KEY,
//   count INTEGER NOT NULL DEFAULT 0,
//   window_start INTEGER NOT NULL
// );
//
// Set the auth secret before deploying:
// wrangler secret put AUTH_KEY

const MAX_BATCH = 500;
const MAX_PAYLOAD_BYTES = 65_536;
const MAX_ITEM_ID_LEN = 256;
const MAX_COUNT = 1_000_000;
const UUID_RE = /^[0-9a-f]{8}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{4}-[0-9a-f]{12}$/i;

const RATE_LIMIT_MAX = 5;
const RATE_LIMIT_WINDOW_MS = 60_000;

export default {
	async fetch(request, env) {
		const url = new URL(request.url);

		if (request.method === 'GET' && url.pathname === '/dashboard') {
			return handleDashboard(url, env);
		}

		if (request.method === 'POST') {
			return handleBatch(request, env);
		}

		return new Response('', { status: 405 });
	}
};

async function handleBatch(request, env) {
	if (request.headers.get('X-BnB-Key') !== env.AUTH_KEY) {
		return new Response('', { status: 403 });
	}

	const contentLength = parseInt(request.headers.get('Content-Length') ?? '0', 10);
	if (contentLength > MAX_PAYLOAD_BYTES) {
		return new Response('', { status: 413 });
	}

	const rawBody = await request.text();
	if (rawBody.length > MAX_PAYLOAD_BYTES) {
		return new Response('', { status: 413 });
	}

	let body;
	try { body = JSON.parse(rawBody); } catch { return new Response('', { status: 400 }); }

	const { userId, entries } = body;
	if (!UUID_RE.test(userId) || !Array.isArray(entries) || entries.length > MAX_BATCH) {
		return new Response('', { status: 400 });
	}

	const ip = request.headers.get('CF-Connecting-IP') ?? request.headers.get('X-Forwarded-For') ?? 'unknown';
	if (await isRateLimited(env, ip, userId)) {
		return new Response('', { status: 429 });
	}

	const now = new Date().toISOString();
	const stmt = env.DB.prepare(
		`INSERT INTO food_counts (user_id, item_id, is_meal, total, last_seen) VALUES (?, ?, ?, ?, ?)
		 ON CONFLICT(user_id, item_id) DO UPDATE SET total = total + excluded.total, last_seen = excluded.last_seen`
	);

	const ops = [];
	for (const entry of entries) {
		const { itemId, isMeal, count } = entry;
		if (
			typeof itemId !== 'string' ||
			itemId.length > MAX_ITEM_ID_LEN ||
			!itemId.includes(':') ||
			!Number.isInteger(count) ||
			count < 1 ||
			count > MAX_COUNT
		) continue;
		ops.push(stmt.bind(userId, itemId, isMeal ? 1 : 0, count, now));
	}

	if (ops.length > 0) await env.DB.batch(ops);

	return new Response('OK');
}

async function isRateLimited(env, ip, userId) {
	const key = `${ip}:${userId}`;
	const windowStart = Math.floor(Date.now() / RATE_LIMIT_WINDOW_MS) * RATE_LIMIT_WINDOW_MS;

	try {
		const row = await env.DB.prepare(
			`INSERT INTO rate_limits (key, count, window_start) VALUES (?, 1, ?)
			 ON CONFLICT(key) DO UPDATE SET
			   count = CASE WHEN window_start = excluded.window_start THEN count + 1 ELSE 1 END,
			   window_start = excluded.window_start
			 RETURNING count`
		).bind(key, windowStart).first();

		return (row?.count ?? 1) > RATE_LIMIT_MAX;
	} catch {
		return false;
	}
}

function escapeHtml(str) {
	return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;').replace(/"/g, '&quot;');
}

async function handleDashboard(url, env) {
	if (url.searchParams.get('key') !== env.DASHBOARD_KEY) {
		return new Response('Forbidden', { status: 403 });
	}

	const exclude = url.searchParams.get('exclude') ?? '';
	const excludeClause = UUID_RE.test(exclude) ? `AND user_id != '${exclude}'` : '';

	const { results: meals } = await env.DB.prepare(
		`SELECT item_id, SUM(total) as total, COUNT(DISTINCT user_id) as users
		 FROM food_counts WHERE is_meal = 1 ${excludeClause} GROUP BY item_id ORDER BY total DESC`
	).all();

	const { results: foods } = await env.DB.prepare(
		`SELECT item_id, SUM(total) as total, COUNT(DISTINCT user_id) as users
		 FROM food_counts WHERE is_meal = 0 ${excludeClause} GROUP BY item_id ORDER BY total DESC`
	).all();

	const { results: userList } = await env.DB.prepare(
		`SELECT user_id, SUM(total) as total FROM food_counts GROUP BY user_id ORDER BY total DESC`
	).all();

	const totalEats = [...meals, ...foods].reduce((s, r) => s + r.total, 0);
	const maxTotal = Math.max(meals[0]?.total ?? 1, foods[0]?.total ?? 1);

	function renderRows(rows) {
		if (rows.length === 0) return '<tr><td colspan="4" style="color:#555">No data yet</td></tr>';
		return rows.map(r => {
			const pct = Math.round((r.total / maxTotal) * 100);
			return `<tr>
				<td>${escapeHtml(r.item_id)}</td>
				<td>${r.total}</td>
				<td>${r.users}</td>
				<td><div class="bar" style="width:${pct}%"></div></td>
			</tr>`;
		}).join('');
	}

	const userRows = userList.map(r => {
		const isExcluded = r.user_id === exclude;
		const link = isExcluded
			? `<a href="?key=${url.searchParams.get('key')}" class="action">unexclude</a>`
			: `<a href="?key=${url.searchParams.get('key')}&exclude=${r.user_id}" class="action">exclude</a>`;
		return `<tr ${isExcluded ? 'style="opacity:.4"' : ''}>
			<td style="font-family:monospace;font-size:.8rem">${escapeHtml(r.user_id)}</td>
			<td>${r.total}</td>
			<td>${link}</td>
		</tr>`;
	}).join('');

	const excludeBanner = UUID_RE.test(exclude)
		? `<div class="banner">Excluding user <code>${escapeHtml(exclude)}</code> from totals. <a href="?key=${url.searchParams.get('key')}">Clear</a></div>`
		: '';

	const html = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Brewing & Baking — Food Log</title>
<style>
  body { font-family: sans-serif; max-width: 960px; margin: 40px auto; padding: 0 20px; background: #111; color: #ddd; }
  h1 { color: #fff; }
  h2 { color: #aaa; font-size: .9rem; text-transform: uppercase; letter-spacing: .08em; margin-top: 2rem; }
  .stat { font-size: 2rem; color: #fff; margin: 4px 0; }
  table { width: 100%; border-collapse: collapse; margin-top: 8px; }
  th { text-align: left; color: #666; font-weight: normal; padding: 6px 8px; border-bottom: 1px solid #2a2a2a; font-size: .85rem; }
  td { padding: 6px 8px; border-bottom: 1px solid #1e1e1e; font-size: .9rem; }
  .bar { height: 10px; background: #4a9eff; border-radius: 2px; min-width: 2px; }
  .grid { display: grid; grid-template-columns: 1fr 1fr; gap: 2rem; }
  .banner { background: #2a1a00; border: 1px solid #554400; color: #ffcc44; padding: 10px 14px; border-radius: 4px; margin: 1rem 0; font-size: .9rem; }
  .banner a, .action { color: #4a9eff; text-decoration: none; }
  .banner a:hover, .action:hover { text-decoration: underline; }
</style>
</head>
<body>
<h1>Brewing & Baking — Food Log</h1>
${excludeBanner}
<h2>Total Eats</h2>
<div class="stat">${totalEats}</div>

<div class="grid">
  <div>
    <h2>Cooking Pot Meals</h2>
    <table>
      <tr><th>Item</th><th>Total</th><th>Users</th><th></th></tr>
      ${renderRows(meals)}
    </table>
  </div>
  <div>
    <h2>Other Foods</h2>
    <table>
      <tr><th>Item</th><th>Total</th><th>Users</th><th></th></tr>
      ${renderRows(foods)}
    </table>
  </div>
</div>

<h2>Users</h2>
<table>
  <tr><th>ID</th><th>Total Eats</th><th></th></tr>
  ${userRows}
</table>
</body>
</html>`;

	return new Response(html, { headers: { 'Content-Type': 'text/html' } });
}
