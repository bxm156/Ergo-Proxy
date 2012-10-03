<h1>Ergo Proxy</h1>
<h2>EECS 425 Computer Networks Project</h2>
---
<h3>Author</h3>
Bryan Marty
<h3>Assignment</h3>
Enterprise networks often employ a Web proxy as an intermediary between the employee browsers and the rest of the Internet.  The proxy can serve multiple roles: block inappropriate websites, monitor employee web surfing, screen incoming pages for malware, and (as mentioned in class) provide a shared cache for Web objects.   In this project, you will develop a simplified Web proxy that accepts HTTP requests from browsers, generates the corresponding HTTP requests for the same objects to the origin servers and forwards the responses to the browsers.
<h3>Special Features</h3>
To save on DNS resolutions, implement internal DNS caching within the proxy: save your DNS resolutions for future use and before you need one, check if you have the required resolution in the cache.  For simplicity, please ignore TTL – just reuse each resolution for the default of up to 30 seconds. 
