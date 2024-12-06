FROM alpine:3.20

RUN set -x \
    && apk upgrade --no-cache \
    && apk add --no-cache bash jq sed lighttpd \
    && rm -rf /var/cache/apk/* \
    && rm -rf /etc/lighttpd/* /etc/logrotate.d/lighttpd /var/log/lighttpd /var/www/localhost \
    && mkdir -p /var/www/html

ADD docker/entrypoint.sh /usr/local/bin/
RUN chmod 755 /usr/local/bin/entrypoint.sh

COPY docker/lighttpd/ /etc/lighttpd/

COPY dist/ /var/www/html/

EXPOSE 80/tcp

HEALTHCHECK --interval=10s --timeout=5s CMD wget -q -T 5 --spider http://127.0.0.1:80/index.html || exit 1

ENTRYPOINT ["/usr/local/bin/entrypoint.sh"]
CMD ["/usr/sbin/lighttpd", "-D", "-f", "/etc/lighttpd/lighttpd.conf"]
