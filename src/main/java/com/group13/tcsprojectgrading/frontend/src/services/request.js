import store from "../redux/store";
import { push } from 'connected-react-router'
import {URL_PREFIX} from "./config";

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}

export function request(url, method = "GET", data = {}, accept = 'application/json', catch401 = true) {
  let init;
  if (method === "GET") {
    init = {
      method: method,
      headers: {
        'Accept': accept,
      },
    }
  } else if (method === "POST" || method === "PUT" || method === "PATCH") {
    init = {
      method: method,
      headers: {
        'Accept': accept,
        'Content-Type': 'application/json',
      },
      body: JSON.stringify(data)
    }
  } else if (method === "DELETE") {
    init = {
      method: method,
      headers: {}
    }
  }

  if (method === 'POST' || method === 'PUT' || method === 'PATCH' || method === 'DELETE') {
    // TODO don't send the token to external urls
    init.headers["X-XSRF-TOKEN"] = getCookie('XSRF-TOKEN');
  }

  return fetch(url, init)
    .then(response => {
      // TODO other responses
      if (response.status === 401 && catch401) {
        store.dispatch(push(URL_PREFIX + "/login/"))
        throw new Error("Not authenticated: 401")
      }
      // TODO: we should catch 404
      else if (response.status === 404) {
        store.dispatch(push(URL_PREFIX + "/404/"))
        throw new Error("Not found: 404")
      }
      else {
        return response;
      }
    })
}
















