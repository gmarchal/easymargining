import requests
import pandas as pd
from pandas.io import json

URL_SERVER = 'http://localhost:8090'
HELLO_PATH = '/'
PRISMA_EUREX_PATH = '/PrismaEurex'


def hello():
    r = requests.get(URL_SERVER + HELLO_PATH)
    return r.text


def etd_margin(sessionid):
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/etdMargin/' + sessionid)
    return r.text


def create_session():
    r = requests.put(URL_SERVER + PRISMA_EUREX_PATH + '/session')
    return r.text


def portfolio():
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/portfolio')
    return r.json


def get_trades(sessionId):
    r = requests.get(URL_SERVER + PRISMA_EUREX_PATH + '/trade/' + sessionId)
    return r.json()


def add_trade(trade):
    r = requests.post(URL_SERVER + PRISMA_EUREX_PATH + '/trade', json=trade)
    return r.text


def add_trades(sessionId, trades):
    r = requests.post(URL_SERVER + PRISMA_EUREX_PATH + '/trade/' + sessionId, data=json.dumps(trades),
                      headers={"content-type": "application/json"})
    return r.text


#session_id = "ff622253-61fa-44b6-b088-885a6483e610"
session_id = create_session()

print(session_id)

# print(hello())

trades = [{'expiryYear': 2022, 'exerciseStyleFlag': 'EUROPEAN', 'expiryDay': None, 'productSettlementType': 'C',
           'exercisePrice': 500.0, 'callPutFlag': 'P', 'expiryMonth': 12, 'longBalance': '0',
           'versionNumber': '0', 'assignedNotifiedBalance': '0', 'shortBalance': '1000',
           'instrumentType': 'OPTION', 'exercisedAllocatedBalance': '0', 'productId': 'ORDX'},
          {'expiryYear': 2015, 'exerciseStyleFlag': 'AMERICAN', 'expiryDay': None, 'productSettlementType': 'D',
           'exercisePrice': 151.5, 'callPutFlag': 'C', 'expiryMonth': 6, 'longBalance': '0',
           'versionNumber': '0', 'assignedNotifiedBalance': '0', 'shortBalance': '1000',
           'instrumentType': 'OPTION', 'exercisedAllocatedBalance': '0', 'productId': 'OGB1'},
          {'expiryYear': 2015, 'exerciseStyleFlag': 'AMERICAN', 'expiryDay': None, 'productSettlementType': 'S',
           'exercisePrice': 23.5, 'callPutFlag': 'C', 'expiryMonth': 6, 'longBalance': '2000',
           'versionNumber': '0', 'assignedNotifiedBalance': '0', 'shortBalance': '0',
           'instrumentType': 'OPTION', 'exercisedAllocatedBalance': '0', 'productId': 'VVU'}]

print(add_trades(session_id, trades))

#print(get_trades(session_id))

#print(add_trade(trade))

print(etd_margin(session_id))
