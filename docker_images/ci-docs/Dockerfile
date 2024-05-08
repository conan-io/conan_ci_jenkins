FROM python:slim

RUN apt-get update \
    && apt-get install -y curl enchant-2 git make latexmk texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra texlive-xetex xindy graphviz

# Download the requirements from the conan docs repo so they stay in sync when we regenerate the image
RUN curl -o /tmp/requirements.txt https://raw.githubusercontent.com/conan-io/docs/master/requirements.txt \
    && pip3 install --requirement /tmp/requirements.txt
