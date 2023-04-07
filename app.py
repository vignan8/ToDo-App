from flask import Flask, render_template, request, redirect, url_for

app = Flask(__name__)


tasks = []


@app.route('/')
def index():
    return render_template('index.html', tasks=tasks)


@app.route('/add', methods=['POST'])
def add():
    task = {
        'title': request.form['title'],
        'priority': request.form['priority']
    }
    tasks.append(task)
    return redirect(url_for('index'))


@app.route('/remove/<int:index>')
def remove(index):
    tasks.pop(index)
    return redirect(url_for('index'))

if __name__ == '__main__':
    app.run(debug=True)
