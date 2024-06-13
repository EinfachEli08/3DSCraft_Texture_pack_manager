package online

class Repository {
    private var _current: String = ""

    var repo: String
        get() = _current
        set(value) {
            _current = value
        }
}

