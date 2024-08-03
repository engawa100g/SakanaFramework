package wasabi

interface WasabiContent<M> : WasabiComponent {
    val master: WasabiContainer<M>
    val index: M

    fun onAdded() {

    }
}