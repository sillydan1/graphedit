# javadoc dockerfile
FROM nginx:alpine
COPY nginx.conf /etc/nginx/nginx.conf
COPY core/build/docs/javadoc /usr/share/nginx/html
EXPOSE 80

