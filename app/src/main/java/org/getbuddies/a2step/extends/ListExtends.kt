package org.getbuddies.a2step.extends

object ListExtends {
    fun <T> ArrayList<T>.append(t: T): ArrayList<T> {
        this.add(t)
        return this
    }
}