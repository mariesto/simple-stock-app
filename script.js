import http from 'k6/http';
import {check} from 'k6';

const USER_COUNT = 100;
const STOCK_COUNT = 10;
const USERS_PER_STOCK = USER_COUNT / STOCK_COUNT;

const users = Array.from({length: USER_COUNT}, (_, i) => `user-${i + 1}`);
const stocks = Array.from({length: STOCK_COUNT}, (_, i) => `STCK-${i + 1}`);

/**
 * `constant_request_rate` is the default test executor.<br>
 * Uncomment the `ramped_arrival_rate` to enable the ramped up test
 */
export const options = {
    scenarios: {
        constant_request_rate: {
          executor: 'constant-arrival-rate',
          rate: 200,
          timeUnit: '1s',
          duration: '30s',
          preAllocatedVUs: 100
        },
        // ramped_arrival_rate: {
        //     executor: 'ramping-arrival-rate',
        //     startRate: 50, // Start with 50 request per second
        //     timeUnit: '1s',
        //     preAllocatedVUs: 100,
        //     maxVUs: 200,
        //     stages: [
        //         {target: 100, duration: '30s'}, // Ramp up to 100 RPS in 30 seconds
        //         {target: 150, duration: '30s'}, // Ramp up to 150 RPS in the next 30s
        //         {target: 300, duration: '30s'}, // Ramp up to 300 RPS in 30 seconds (peak load)
        //         {target: 300, duration: '30s'}, // Hold at 300 RPS for 30 seconds
        //         {target: 0, duration: '30s'}  // Ramp down to 0 RPS in 30 seconds
        //     ],
        //     gracefulStop: '10s',
        // },
    }
}

export function setup() {
    return {users, stocks};
}

function getStockForUser(userId, stocksData) {
    const userNumber = parseInt(userId.split('-')[1]);
    const stockIndex = Math.floor((userNumber - 1) / USERS_PER_STOCK);
    return stocksData[stockIndex];
}

export default function (data) {
    const userIndex = (__VU - 1) % USER_COUNT;
    const user = data.users[userIndex];
    const stock = getStockForUser(user, data.stocks);

    let payload = JSON.stringify({
        userId: user,
        quantity: 3,
        stockSymbol: stock,
        tradeType: 'BUY'
    })

    let url = 'http://localhost:8080/api/v1/stocks/trade';
    let params = {headers: {'Content-Type': 'application/json'}};

    let res = http.post(url, payload, params);

    check(res, {'status is 200': (r) => r.status === 200});

}