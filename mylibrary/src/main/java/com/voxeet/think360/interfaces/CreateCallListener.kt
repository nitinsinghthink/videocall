package com.voxeet.think360.interfaces

import com.voxeet.promise.solve.Solver
import java.util.*

interface CreateCallListener {
    fun call(objects: Solver<Any>)
    fun failure(message : String)
}