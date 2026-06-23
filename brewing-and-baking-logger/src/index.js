export default {
	async fetch(request, env) {
		if (request.method !== 'POST') return new Response('', { status: 405 });

		let body;
		try { body = await request.json(); } catch { return new Response('', { status: 400
		}); }

		const { itemId, timestamp } = body;
		if (typeof itemId !== 'string' || !itemId.startsWith('brewingandbaking:')) {
			return new Response('', { status: 400 });
		}

		await env.DB.prepare('INSERT INTO food_events (item_id, ts) VALUES (?, ?)')
			.bind(itemId, timestamp ?? new Date().toISOString())
			.run();

		return new Response('OK');
	}
};
