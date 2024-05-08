FROM python:slim

RUN apt-get update \
    && apt-get install -y enchant-2 git make latexmk texlive-latex-base texlive-fonts-recommended texlive-fonts-extra texlive-latex-extra texlive-xetex xindy graphviz

# Install the requirements from the conan docs repo so they stay in sync when we regenerate the image
RUN pip3 install --requirement https://raw.githubusercontent.com/conan-io/docs/master/requirements.txt
