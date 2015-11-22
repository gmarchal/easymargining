import requests
import pandas as pd
from pandas.io import json

URL_SERVER = 'http://localhost:8080'
HELLO_PATH = '/'
PRISMA_EUREX_PATH = '/PrismaEurex'


def hello():
    r = requests.get(URL_SERVER + HELLO_PATH)
    return r.text


def etd_margin():
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/etdMargin')
    return r.text


def portfolio():
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/portfolio')
    return r.json


def get_trade():
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/trade')
    return r.json()


def add_trade(trade):
    r = requests.post(URL_SERVER + PRISMA_EUREX_PATH + '/trade', json=trade)
    return r.text


print(hello())
print(get_trade())

trade = {'assignedNotifiedBalance': None, 'exerciseStyleFlag': None, 'expiryDate': None, 'longBalance': None,
         'productSettlementType': None, 'shortBalance': None, 'exercisedAllocatedBalance': None, 'instrumentType': None,
         'productId': 'ORDX', 'versionNumber': None, 'exercisePrice': None, 'callPutFlag': None}

print(add_trade(trade))

print(etd_margin())
