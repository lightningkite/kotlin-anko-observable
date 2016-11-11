# kotlin-anko-observable

A set of extension functions that use features from both [kotlin-anko](https://github.com/lightningkite/kotlin-anko) and [kotlin-observable](https://github.com/lightningkite/kotlin-observable).

There are two primary things this repository is for: adapters and data binding.

## Data Binding

A collection of extension functions on various views that makes it so a view can reflect a piece of data correctly at all times, and even have the view modify the data.

```kotlin
//Declare an observable property
val textObs = StandardObservableProperty("Start Text")

//later on in your Anko
editText {
  styleDefault()

  //This ensures that the value of the observable and the text inside this EditText are always the same.
  //If textObs changes, so does this.  If this EditText changes, so does textObs.
  bindString(textObs)
}.lparams(matchParent, wrapContent) { margin = dip(8) }

textView {
  //Always shows the value of textObs, updating the view when needed.
  bindString(textObs)
}.lparams(matchParent, wrapContent) { margin = dip(8) }

textView {
  //Equivalent to the above without using the convenience function
  lifecycle.bind(textObs){ it ->
    text = it
  }
}.lparams(matchParent, wrapContent) { margin = dip(8) }
```

## Adapters

This library makes the creation of adapters in Anko WAY easier by using observables.  Here's an example:

```kotlin
verticalRecyclerView {
  //Create and use an adapter using the list `items`.
  adapter = listAdapter(items) { obs ->
  
    //Create the view used for each item
    textView {
      gravity = Gravity.CENTER
      textSize = 18f
      minimumHeight = dip(40)
      backgroundResource = selectableItemBackgroundResource

      //updates the text in this TextView when the item changes
      lifecycle.bind(obs){ item ->
        text = item.toString()
      }

      onLongClick {
        items.removeAt(obs.position)
        true
      }
    }.lparams(matchParent, wrapContent)
  }
}
```
