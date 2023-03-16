/**  url: the URL to send the HTTP request to
method: the HTTP method (e.g. 'GET', 'POST', 'PUT')
data: an optional object to send as the request body 
*/
const fetchData = async (url, method) => {
    const options = {
      method: method,
      headers: {
        'Content-Type': 'application/json'
      }
    };
    
    /*if (data) {
      options.body = JSON.stringify(data);
    }*/
    
    return fetch(url, options)
    .then((response)=>response.json())
    .then((response)=> {return response})
    .catch(error => console.warn(error));
}
export default fetchData;