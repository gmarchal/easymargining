import requests

URL_SERVER = 'http://localhost:8080'
HELLO_PATH = '/'
PRISMA_EUREX_PATH = '/PrismaEurex'


def hello():
    r = requests.get(URL_SERVER + HELLO_PATH)
    return r.text


def etd_margin():
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/etdMargin')
    return r.text


print(etd_margin())
