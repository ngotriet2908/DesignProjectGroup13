import store from "../redux/store";
import { push } from 'connected-react-router'
import {URL_PREFIX} from "./config";

function getCookie(name) {
  const value = `; ${document.cookie}`;
  const parts = value.split(`; ${name}=`);
  if (parts.length === 2) return parts.pop().split(';').shift();
}

export function request(
  url,
  method = "GET",
  data = {},
  accept = 'application/json',
  catch401 = true,
  catch404 = true,
  formData = undefined,
) {
  let init;
  if (method === "GET") {
    init = {
      method: method,
      headers: {
        'Accept': accept,
      },
    }
  } else if (method === "POST" || method === "PUT" || method === "PATCH") {
    if (formData !== undefined) {
      init = {
        method: method,
        headers: {
          'Accept': accept,
          // 'Content-Type': 'multipart/form-data',
        },
        body: formData
      }
    } else {
      init = {
        method: method,
        headers: {
          'Accept': accept,
          'Content-Type': 'application/json',
        },
        body: JSON.stringify(data)
      }
    }
  } else if (method === "DELETE") {
    init = {
      method: method,
      headers: {}
    }
  }

  if (method === 'POST' || method === 'PUT' || method === 'PATCH' || method === 'DELETE') {
    init.headers["X-XSRF-TOKEN"] = getCookie('XSRF-TOKEN');
  }

  return fetch(url, init)
    .then(response => {
      if (response.status === 401 && catch401) {
        store.dispatch(push(URL_PREFIX + "/login/"))
        throw new Error("Not authenticated: 401")
      }
      else if (catch404 && response.status === 404) {
        store.dispatch(push(URL_PREFIX + "/404/"))
        throw new Error("Not found: 404")
      }
      else {
        return response;
      }
    })
}
















