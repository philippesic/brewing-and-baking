export default {
	async fetch(request, env) {
		const url = new URL(request.url);

		if (request.method === 'GET' && url.pathname === '/dashboard') {
			return handleDashboard(url, env);
		}

		if (request.method === 'POST') {
			return handleLog(request, env);
		}

		return new Response('', { status: 405 });
	}
};

async function handleLog(request, env) {
	let body;
	try { body = await request.json(); } catch { return new Response('', { status: 400 }); }

	const { itemId, timestamp } = body;
	if (typeof itemId !== 'string' || !itemId.startsWith('brewingandbaking:')) {
		return new Response('', { status: 400 });
	}

	await env.DB.prepare('INSERT INTO food_events (item_id, ts) VALUES (?, ?)')
		.bind(itemId, timestamp ?? new Date().toISOString())
		.run();

	return new Response('OK');
}

async function handleDashboard(url, env) {
	if (url.searchParams.get('key') !== env.DASHBOARD_KEY) {
		return new Response('Forbidden', { status: 403 });
	}

	const { results: totals } = await env.DB.prepare(
		'SELECT item_id, COUNT(*) as cnt FROM food_events GROUP BY item_id ORDER BY cnt DESC'
	).all();

	const { results: recent } = await env.DB.prepare(
		'SELECT item_id, ts FROM food_events ORDER BY ts DESC LIMIT 50'
	).all();

	const totalEvents = totals.reduce((sum, r) => sum + r.cnt, 0);
	const maxCnt = totals[0]?.cnt ?? 1;

	const rows = totals.map(r => {
		const name = r.item_id.replace('brewingandbaking:', '');
		const pct = Math.round((r.cnt / maxCnt) * 100);
		return `
		<tr>
			<td>${name}</td>
			<td>${r.cnt}</td>
			<td><div class="bar" style="width:${pct}%"></div></td>
		</tr>`;
	}).join('');

	const recentRows = recent.map(r => {
		const name = r.item_id.replace('brewingandbaking:', '');
		const date = new Date(r.ts).toLocaleString();
		return `<tr><td>${name}</td><td>${date}</td></tr>`;
	}).join('');

	const html = `<!DOCTYPE html>
<html lang="en">
<head>
<meta charset="UTF-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Brewing & Baking — Food Log</title>
<style>
  body { font-family: sans-serif; max-width: 900px; margin: 40px auto; padding: 0 20px; background: #111; color: #ddd; }
  h1 { color: #fff; }
  h2 { color: #aaa; font-size: 1rem; margin-top: 2rem; }
  .stat { font-size: 2rem; color: #fff; margin: 4px 0; }
  table { width: 100%; border-collapse: collapse; margin-top: 8px; }
  th { text-align: left; color: #888; font-weight: normal; padding: 6px 8px; border-bottom: 1px solid #333; }
  td { padding: 6px 8px; border-bottom: 1px solid #222; }
  .bar { height: 12px; background: #4a9eff; border-radius: 2px; min-width: 2px; }
</style>
</head>
<body>
<h1>Brewing & Baking — Food Log</h1>
<h2>TOTAL EVENTS</h2>
<div class="stat">${totalEvents}</div>

<h2>BY FOOD</h2>
<table>
  <tr><th>Item</th><th>Count</th><th></th></tr>
  ${rows}
</table>

<h2>RECENT (last 50)</h2>
<table>
  <tr><th>Item</th><th>Time</th></tr>
  ${recentRows}
</table>
</body>
</html>`;

	return new Response(html, { headers: { 'Content-Type': 'text/html' } });
}
