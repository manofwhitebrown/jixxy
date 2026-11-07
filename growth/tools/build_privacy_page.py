#!/usr/bin/env python3
"""Render PRIVACY.md to site/privacy.html (static, zero external requests).
Run from the repo root: python3 growth/tools/build_privacy_page.py
Needs: pip install markdown. Re-run after every PRIVACY.md change."""
import re, markdown
src = open('PRIVACY.md', encoding='utf-8').read()
body = markdown.markdown(src, extensions=['tables'])
body = re.sub(r'href="(?!https?:|#|mailto:)[^"]*"', 'href="#"', body)  # repo-relative links do not exist on the site
page = f'''<!doctype html>
<html lang="en">
<head>
<meta charset="utf-8">
<meta name="viewport" content="width=device-width, initial-scale=1">
<title>Jixxy privacy notice</title>
<meta name="description" content="What Jixxy does and does not do with your data. Plain language. Draft.">
<meta name="color-scheme" content="light dark">
<link rel="stylesheet" href="style.css">
</head>
<body>
<a class="skip" href="#main">Skip to content</a>
<main id="main" class="wrap">
<p><a href="index.html">&larr; Back to Jixxy</a></p>
{body}
</main>
<footer class="wrap foot">
<p>Generated from PRIVACY.md in the repository. This is a draft description, not legal advice.</p>
</footer>
</body>
</html>
'''
open('site/privacy.html', 'w', encoding='utf-8').write(page)
print('wrote site/privacy.html', len(page), 'bytes')
