from flask import Flask, request
from flask_restful import Resource, Api
import json

app = Flask(__name__)
api = Api(app)

players = {}

print("Loading last file (if exists)...")

with open("players.txt",encoding='utf-8-sig', errors='ignore') as f:
     players = json.loads(f.read().replace("'", '"'))#.replace('"true"',"true").replace('"false"',"false"), strict=False)
#print(players)

class Players(Resource):
    def get(self, uuid):
        return {uuid: players[uuid]}

    def put(self, uuid):
        players[uuid] = request.form['data']
        file = open("players.txt","w")
        file.write(str(players))
        file.close
        print(str(players))
        return {uuid: players[uuid]}

api.add_resource(Players, '/<string:uuid>')

if __name__ == '__main__':
    app.run()
