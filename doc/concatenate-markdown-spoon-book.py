#!/usr/bin/python3

import sys

import commonmark
import codecs
from pprint import pprint
import frontmatter
import glob
from yaml import load, dump, Loader


def parse(filename):
  #codecs.open(filename, encoding="utf8").read()
  post = frontmatter.load(filename)
  print("\n\n## "+post.metadata['title'])
  print(post.content)
  # for outline
  ast = commonmark.Parser().parse(post.content)
  #for i,_ in ast.walker(): 
      ##print(i.__dict__['t'])
      #if i.parent and i.parent.t == "heading":
          ##pprint(vars(i.parent))
          ##pprint(vars(i))
          #for indent in range(0,i.parent.level): print("#", end='')
          #print(' ', end='')
          ##for indent in range(0,i.parent.level): print("  ", end='')
          #print(i.literal)


# order is in _data/sidebar_doc.yml
sidebar = load(open('_data/sidebar_doc.yml'), Loader=Loader)

for entry in sidebar['entries']:
  for cat in entry['subcategories']:
    print("\n\n\n# "+cat['title'])
    for item in cat['items']:
      if 'url' in item and '#' not in item['url']:
        md_file = item['url'][1:-5]+".md"
        parse(md_file)


#filename = sys.argv[1]

