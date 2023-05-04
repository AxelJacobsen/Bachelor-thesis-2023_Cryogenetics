/**  url: the URL to send the HTTP request to
method: the HTTP method (e.g. 'GET', 'POST', 'PUT')
data: an optional object to send as the request body 
*/
const fetchData = async (url, method, data) => {
  const options = {
    method: method,
    headers: {
      'Content-Type': 'application/json'
    }
  };

  if (data) {
    options.body = JSON.stringify(data);
  }
  const response = await fetch(url, options);

  if (!response.ok) {
    if (response.status === 409) {
      throw new Error("An object with that identification already exists");
    } else {
      throw new Error(`HTTP error! Status: ${response.status}.`);
    }
  }

  return response.json();
};

export default fetchData;