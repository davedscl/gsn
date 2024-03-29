# See https://docs.djangoproject.com/en/1.8/ref/settings/#databases

DATABASES = {
    'default': {
        'ENGINE': 'django.db.backends.sqlite3',
        'NAME': 'db.sqlite3', }
}

GSN = {
    'CLIENT_ID': 'web-gui-public',
    'CLIENT_SECRET': 'web-gui-public',
    'SERVICE_URL_PUBLIC': 'http://localhost:9000/ws/', # used for in-browser redirects
    'SERVICE_URL_LOCAL': 'http://localhost:9000/ws/',  # used for on-server direct calls
    'WEBUI_URL': 'http://localhost:4200/',             # used for in-browser redirects
    'MAX_QUERY_SIZE': 5000,
}