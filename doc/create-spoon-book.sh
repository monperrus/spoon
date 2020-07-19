#!/bin/bash
# creates the PDF of the Spoon user manual
#
# prerequisites: Python 3
#
# usage: 
# cd doc
# ./create-spoon-book.sh

# created with git shortlog -n -s -- doc
AUTHORS="Martin Monperrus\and Gerard Paligot\and Simon Urli\and Nicolas Harrand\and Pavel Vojtechovsky\and Gérard Paligot\and Thomas Durieux\and Egor Bredikhin\and Martin Witt\and Alexander Shopov\and Benjamin Danglot\and et. al."

VERSION="version of "`date "+%Y-%m-%d"`", commit "`git rev-parse HEAD`

cat <<EOF > spoon-user-manual.md
---
title: "Spoon User Manual"
author: $AUTHORS
date: $VERSION
geometry: a4paper,margin=2.5cm
output: pdf_document
colorlinks: true
urlcolor: blue
header-includes:
  - \usepackage{xcolor}
  - \definecolor{codegreen}{rgb}{0,0.6,0}
  - \definecolor{codegray}{rgb}{0.5,0.5,0.5}
  - \definecolor{codepurple}{rgb}{0.58,0,0.82}
  - \definecolor{backcolour}{rgb}{0.95,0.95,0.92}
  - \lstset{backgroundcolor=\color{backcolour}}
  - \lstset{commentstyle=\color{codegreen}}
  - \lstset{keywordstyle=\color{magenta}}
  - \lstset{numberstyle=\tiny\color{codegray}}
  - \lstset{stringstyle=\color{codepurple}}
  - \lstset{breaklines=true}
  - \lstset{showspaces=false}
  - \lstset{language=Java}
  - \lstset{basicstyle=\ttfamily\footnotesize}
---

# Introduction

This PDF is generated from the markdown files that are in <https://github.com/INRIA/spoon/tree/master/doc>

If you notice an error, it would be great if you can do a pull-request on the corresponding files.

An update of this PDF is done regularly\footnote{with \texttt{create-spoon-book.sh} at \url{https://github.com/monperrus/spoon/blob/create-spoon-book.sh/doc/create-spoon-book.sh}}.

If you need professional support (training, consultancy) on Spoon, post a comment on <https://github.com/INRIA/spoon/issues/3251>

EOF


python3 concatenate-markdown-spoon-book.py  >> spoon-user-manual.md

pandoc --table-of-contents --listings -s -o spoon-user-manual.tex spoon-user-manual.md

lualatex spoon-user-manual.tex

md2outline spoon-user-manual.md

rm spoon-user-manual.md
