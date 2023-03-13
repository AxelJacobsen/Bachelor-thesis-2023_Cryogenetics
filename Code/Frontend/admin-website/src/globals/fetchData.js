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
    
    const response = await fetch(url, options);
    const responseData = await response.json();
    
    if (!response.ok) {
      throw new Error(responseData.message || 'Something went wrong!');
    }
    
    return responseData;
}
export default fetchData;