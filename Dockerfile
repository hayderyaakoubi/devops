FROM ubuntu:latest
LABEL authors="marwe"

ENTRYPOINT ["top", "-b"]