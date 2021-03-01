import store from "../redux/store";
import { push } from 'connected-react-router'
import {URL_PREFIX} from "./config";

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}

export function request(url, method = "GET", data = {}) {
  let init;
  if (method === "GET") {
    init = {
      method: method,
      headers: {
        'Accept': 'application/json',
      },
    }
  } else if (method === "POST") {
    init = {
      method: method,
      headers: {
        'Accept': 'application/json',
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    }
  }

  if (method === 'POST' || method === 'PUT' || method === 'DELETE') {
    // TODO don't send the token to external urls
    init.headers["X-XSRF-TOKEN"] = getCookie('XSRF-TOKEN');
  }

  let promise = fetch(url, init);

  return promise
    .then(response => {
      // TODO other responses
      if (response.status === 401) {
        store.dispatch(push(URL_PREFIX + "/login/"))
        throw new Error("Not authenticated: 401")
      } else {
        return response;
      }
    })
}
















