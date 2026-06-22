import http from "k6/http";
import { check, fail, sleep } from "k6";

const BASE_URL = (__ENV.BASE_URL || "http://localhost:8080").replace(/\/+$/, "");
const USER_ID = __ENV.USER_ID || "admin01";
const PASSWORD = __ENV.PASSWORD || "password";
const PROJECT_ID = __ENV.PROJECT_ID || "1";
const PROFILE = __ENV.K6_PROFILE || "load";
const THINK_TIME_SECONDS = Number(__ENV.THINK_TIME_SECONDS || "1");

const profiles = {
  smoke: [
    { duration: "5s", target: 1 },
    { duration: "10s", target: 5 },
    { duration: "5s", target: 0 }
  ],
  load: [
    { duration: "30s", target: 10 },
    { duration: "1m", target: 50 },
    { duration: "1m", target: 100 },
    { duration: "5m", target: 100 },
    { duration: "1m", target: 0 }
  ]
};

if (!profiles[PROFILE]) {
  throw new Error(`Unsupported K6_PROFILE '${PROFILE}'. Use 'smoke' or 'load'.`);
}

export const options = {
  scenarios: {
    projectCache: {
      executor: "ramping-vus",
      startVUs: 0,
      stages: profiles[PROFILE],
      gracefulRampDown: "30s"
    }
  },
  thresholds: {
    "http_req_failed{endpoint:projectById}": ["rate<0.01"],
    "http_req_duration{endpoint:projectById}": ["p(95)<500", "p(99)<1000"],
    "checks{endpoint:projectById}": ["rate>0.99"]
  }
};

export function setup() {
  const payload = JSON.stringify({
    userId: USER_ID,
    password: PASSWORD
  });

  let loginResponse;

  for (let attempt = 1; attempt <= 30; attempt += 1) {
    loginResponse = http.post(`${BASE_URL}/api/auth/login`, payload, {
      headers: { "Content-Type": "application/json" },
      tags: { endpoint: "loginSetup" }
    });

    if (loginResponse.status === 200) {
      const responseBody = loginResponse.json();
      const token = responseBody && responseBody.data && responseBody.data.accessToken;

      if (token) {
        return { token, projectId: PROJECT_ID };
      }
    }

    sleep(2);
  }

  fail(`Login failed for ${USER_ID}. Last status: ${loginResponse && loginResponse.status}`);
}

export default function (data) {
  const response = http.get(`${BASE_URL}/api/projects/${data.projectId}`, {
    headers: {
      Authorization: `Bearer ${data.token}`
    },
    tags: {
      endpoint: "projectById"
    }
  });

  check(
    response,
    {
      "project response is 200": (result) => result.status === 200,
      "project id matches": (result) =>
        result.status === 200 && String(result.json("data.id")) === String(data.projectId)
    },
    { endpoint: "projectById" }
  );

  sleep(THINK_TIME_SECONDS);
}
