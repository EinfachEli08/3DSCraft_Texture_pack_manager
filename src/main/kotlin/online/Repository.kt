package online

data class Repo(val url: String, var placeholder: String) // placeholder is now mutable

class Repository {
    private val _repoList = ArrayList<Repo>()

    val repoList: List<Repo>
        get() = _repoList

    fun addRepo(repo: Repo) {
        _repoList.add(repo)
    }

    fun getRepo(index: Int): Repo {
        return _repoList[index]
    }

    fun getRepoLength(): Int {
        return _repoList.size
    }

    fun removeRepoAtIndex(index: Int) {
        if (index in 0 until _repoList.size) {
            _repoList.removeAt(index)
        }
    }
}