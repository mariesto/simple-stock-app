import http from 'k6/http';
import { check, sleep } from 'k6';

const USER_COUNT = 100;
const STOCK_COUNT = 10;
const USERS_PER_STOCK = USER_COUNT / STOCK_COUNT;

const users = Array.from({ length: USER_COUNT }, (_, i) => `user-${i + 1}`);
const stocks = Array.from({ length: STOCK_COUNT }, (_, i) => `STCK-${i + 1}`);

export const options = {
  scenarios: {
    constant_request_rate: {
      executor: 'constant-arrival-rate',
      rate: 200,
      timeUnit: '1s',
      duration: '30s',
      preAllocatedVUs: 100
    }
  }
}

export function setup() {
  return { users, stocks };
}

function getStockForUser(userId, stocksData) {
  const userNumber = parseInt(userId.split('-')[1]);
  const stockIndex = Math.floor((userNumber - 1) / USERS_PER_STOCK);
  return stocksData[stockIndex];
}

export default function (data){
  // Select user in sequence: user-1, user-2,... user-100, user-1...
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
  let params = { headers: { 'Content-Type': 'application/json' } };

  let res = http.post(url, payload, params);

  check(res, { 'status is 200': (r) => r.status === 200 });

}