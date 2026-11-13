const trimTrailingSlash = (value) => value.replace(/\/+$/, '');

const config = {
  AUTH_URL: trimTrailingSlash(
    process.env.REACT_APP_AUTH_URL || 'http://localhost:9090'
  ),
  CATALOGO_URL: trimTrailingSlash(
    process.env.REACT_APP_CATALOGO_URL || 'http://localhost:9092'
  ),
  REQUEST_TIMEOUT_MS: Number(
    process.env.REACT_APP_REQUEST_TIMEOUT_MS || 15000
  ),
};

export default config;
