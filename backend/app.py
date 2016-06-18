import datetime
import functools
import os
import re
import urllib
import bcrypt

from flask import (Flask, flash, Markup, redirect, render_template, request,
                   Response, session, url_for)

from markdown import markdown
from markdown.extensions.codehilite import CodeHiliteExtension
from markdown.extensions.extra import ExtraExtension

from micawber import bootstrap_basic, parse_html
from micawber.cache import Cache as OEmbedCache

from peewee import *

from playhouse.flask_utils import FlaskDB, get_object_or_404, object_list
from playhouse.fields import PasswordField
from playhouse.sqlite_ext import *


APP_DIR = os.path.dirname(os.path.realpath(__file__))

DATABASE = 'sqliteext:///%s' % os.path.join(APP_DIR, 'geopubsub.db')

DEBUG = True

SECRET_KEY = 'shhh, secret!'

SITE_WIDTH = 800


app = Flask(__name__)
app.config.from_object(__name__)

flask_db = FlaskDB(app)

database = flask_db.database

oembed_providers = bootstrap_basic(OEmbedCache())

class Company(flask_db.Model):
    name = CharField()
    email = CharField(unique=True)
    password = PasswordField()
    ctype = IntegerField()

class Slogan(flask_db.Model):
    company = ForeignKeyField(Company)
    text = CharField()
    timestamp = DateTimeField(default=datetime.datetime.now, index=True)



# DECORATORS
def login_required(fn):
    @functools.wraps(fn)
    def inner(*args, **kwargs):
        if session.get('logged_in'):
            return fn(*args, **kwargs)
        return redirect(url_for('login', next=request.path))
    return inner


def not_user_page(fn):
    @functools.wraps(fn)
    def inner(*args, **kwargs):
        if session.get('logged_in'):
            return redirect(url_for('dashboard'))
        return fn(*args, **kwargs)
    return inner


# ROUTES
@app.route('/')
def index():
    return render_template('index.html')


@app.route('/kayit-ol/', methods=['GET', 'POST'])
@not_user_page
def signup():
    if request.method == 'POST':
        if len(set(['cname', 'email', 'password', 'ctype']) & set(request.form)):
            Company.create(
                name=request.form.get('cname'),
                email=request.form.get('email'),
                password=request.form.get('password'),
                ctype=request.form.get('ctype')
            )  

            # TODO: Redirect after signup to dashboard page
            # session['logged_in'] = True
            # session.permanent = True  # Use cookie to store session.

            # redirect(url_for('dashboard'))
        else:
            flash('Hata', 'danger')
    return render_template('signup.html')


@app.route('/giris/', methods=['GET', 'POST'])
@not_user_page
def login():
    next_url = request.args.get('next') or request.form.get('next')
    if request.method == 'POST' and request.form.get('password') and request.form.get('email'):
        email = request.form.get('email')
        password = request.form.get('password')
        try:
            c = Company.get(Company.email==email)
            
            if c.password.check_password(password):
                session['logged_in'] = True
                session['company_name'] = c.name
                session['company_id'] = c.id
                session['company_type'] = c.ctype
                session.permanent = True
            
                return redirect(next_url or url_for('dashboard'))
            else:
                flash('password yanlis', 'danger')    
        except Company.DoesNotExist:
            flash('boyle bir email yok', 'danger')
    return render_template('login.html', next_url=next_url)


@app.route('/cikis/', methods=['GET', 'POST'])
def logout():
    session.clear()
    return redirect(url_for('login'))


@app.route('/panel')
@login_required
def dashboard():
    query = Slogan.select().order_by(Slogan.timestamp.asc())
    return object_list(
        'dashboard.html',
        query,
        company_name=session['company_name'],
        check_bounds=False)


@app.route('/guncelle/')
@login_required
def settings():
    return render_template('settings.html', company_name=session['company_name'], check_bounds=False)


@app.route('/slogan', methods=['GET', 'POST'])
def add_slogan():
    if session.get('logged_in') and request.form.get('slogan'):
        c = Company.get(Company.id==session['company_id'])
        Slogan.create(company=c, text=request.form.get('slogan'))
        
    return redirect(url_for('dashboard'))


@app.route('/slogan/guncelle', methods=['GET', 'POST'])
def edit_slogan():
    slogan = request.form.get('slogan', None)
    slogan_id = request.form.get('hidden-slogan-id', None)

    if session.get('logged_in') and slogan and slogan_id:
        try:
            s = Slogan.get(Slogan.id == slogan_id)
            s.text = slogan
            s.save()

        except Slogan.DoesNotExist:
            pass

    return redirect(url_for('dashboard'))


@app.route('/slogan/sil', methods=['GET', 'POST'])
def delete_slogan():
    slogan_id = request.get_json().get('slogan', None)

    if session.get('logged_in') and slogan_id:
        try:
            s = Slogan.get(Slogan.id == slogan_id)
            s.delete_instance()

        except Slogan.DoesNotExist:
            pass

    return Response('ok'), 200


@app.errorhandler(404)
def not_found(exc):
    return Response('<h3>Not found</h3>'), 404


# MAIN HANDLER
def main():
    database.create_tables([Company, Slogan], safe=True)
    app.run(debug=DEBUG)

if __name__ == '__main__':
    main()
