FROM python:slim

RUN apt-get update \
    && apt-get install -y enchant-2 git make latexmk texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra texlive-xetex graphviz

COPY requirements.txt /tmp/

RUN pip3 install --requirement /tmp/requirements.txt