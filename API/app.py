from flask import Flask, request, jsonify
from flask_sqlalchemy import SQLAlchemy
from flask_marshmallow import Marshmallow
from flask_restful import Resource, Api
import os

app = Flask(__name__)
api = Api(app)
app.config['SQLALCHEMY_DATABASE_URI'] = 'sqlite:///players.db'
app.config['SQLALCHEMY_TRACK_MODIFICATIONS'] = False
db = SQLAlchemy(app)
ma = Marshmallow(app)


class User(db.Model):
    id = db.Column(db.Integer)
    uuid = db.Column(db.String(32), unique=True, primary_key = True)
    online = db.Column(db.Boolean())

    def __init__(self, uuid, online):
        self.uuid = uuid
        self.online = online

class UserSchema(ma.Schema):
    class Meta:
        fields = ('id', 'uuid', 'online')

user_schema = UserSchema()
users_schema = UserSchema(many=True)

class UserManager(Resource):

    @staticmethod
    def get():
        try: uuid = request.args['uuid']
        except Exception as _: uuid = None

        if not uuid:
            #users = User.query.all()
            #return jsonify(users_schema.dump(users))
            return jsonify({ 'Message': 'Failure! Must provide the user UUID' })
        try:
            user = User.query.get(uuid)
        except Exception as _:
            return jsonify({ 'Message': 'Failure! The provided User was not found!'})
        return jsonify(user_schema.dump(user))

    @staticmethod
    def post():
        uuid = request.json['uuid']
        online = request.json['online']

        user = User(uuid, online)
        db.session.add(user)
        db.session.commit()
        return jsonify({'Message':'Success! User '+uuid+' inserted.'})

    @staticmethod
    def delete():
        try: uuid = request.args['uuid']
        except Exception as _: uuid = None
        if not uuid:
            return jsonify({ 'Message': 'Failure! Must provide the user UUID' })
        user = User.query.get(uuid)

        db.session.delete(user)
        db.session.commit()

        return jsonify({'Message':'Success! User '+uuid+' deleted.'})


api.add_resource(UserManager, '/axolotlclient-api')


if __name__ == '__main__':
    app.run(debug=True)
