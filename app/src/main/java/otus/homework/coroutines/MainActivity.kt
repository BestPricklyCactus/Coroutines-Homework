package otus.homework.coroutines

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.observeOn
import kotlinx.coroutines.launch
import otus.homework.coroutines.model.Model

class MainActivity : AppCompatActivity() {

    lateinit var catsPresenter: CatsPresenter
    private val diContainer = DiContainer()
    private var isPresenretMode = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val view = layoutInflater.inflate(R.layout.activity_main, null) as CatsView
        setContentView(view)
        if (isPresenretMode) {
            catsPresenter = CatsPresenter(
                diContainer.service,
                diContainer.imagesService,
                ::onShowToast
            )

            view.presenter = catsPresenter
            catsPresenter.attachView(view)
            catsPresenter.onInitComplete()
        } else {
            val viewModel = CatsViewModel(
                diContainer.service,
                diContainer.imagesService,
                ::onShowToast
            )
            viewModel.onInitComplete()
            view.viewModel = viewModel
            lifecycleScope.launch {
                viewModel.state.collect { state ->
                    when (state) {
                        is Result.Success<*> -> {
                            if (state.data is Model) {
                                view.populate(Model(state.data.fact, state.data.imageUrl))
                            }
                        }

                        is Result.Error -> {
                            onShowToast(state.msg)
                        }
                    }
                }
            }
        }
    }

    private fun onShowToast(message: String?) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    override fun onStop() {
        if (isFinishing) {
            catsPresenter.detachView()
            catsPresenter.onStop()
        }
        super.onStop()
    }

}