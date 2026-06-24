import {
	env,
	createExecutionContext,
	waitOnExecutionContext,
} from "cloudflare:test";
import { describe, it, expect } from "vitest";
import worker from "../src";

const VALID_UUID = "11111111-1111-1111-1111-111111111111";

async function post(body, headers = {}) {
	const ctx = createExecutionContext();
	const res = await worker.fetch(
		new Request("http://example.com", {
			method: "POST",
			headers: { "Content-Type": "application/json", ...headers },
			body: JSON.stringify(body),
		}),
		env,
		ctx
	);
	await waitOnExecutionContext(ctx);
	return res;
}

describe("auth", () => {
	it("rejects missing key with 403", async () => {
		const res = await post({ userId: VALID_UUID, entries: [] });
		expect(res.status).toBe(403);
	});

	it("rejects wrong key with 403", async () => {
		const res = await post({ userId: VALID_UUID, entries: [] }, { "X-BnB-Key": "wrong" });
		expect(res.status).toBe(403);
	});
});

describe("validation", () => {
	it("rejects invalid userId with 400", async () => {
		const res = await post({ userId: "not-a-uuid", entries: [] }, { "X-BnB-Key": env.AUTH_KEY });
		expect(res.status).toBe(400);
	});

	it("rejects oversized entry list with 400", async () => {
		const entries = Array.from({ length: 501 }, (_, i) => ({
			itemId: `mod:item_${i}`,
			isMeal: false,
			count: 1,
		}));
		const res = await post({ userId: VALID_UUID, entries }, { "X-BnB-Key": env.AUTH_KEY });
		expect(res.status).toBe(400);
	});
});

describe("routing", () => {
	it("rejects GET to batch endpoint with 405", async () => {
		const ctx = createExecutionContext();
		const res = await worker.fetch(new Request("http://example.com"), env, ctx);
		await waitOnExecutionContext(ctx);
		expect(res.status).toBe(405);
	});
});
