package com

import scala.collection.generic.CanBuildFrom
import scala.util.Random
import scala.collection.mutable.ArrayBuffer

class GA (popSize: Int, genMax: Int, tSize: Int) extends Optimizer {
    var averages : Array[Double] = null;
    var bests    : Array[Double] = null;

    def apply(p: Problem): (Seq[Double], p.SolutionType) = {
        averages = Array.ofDim[Double](genMax)
        bests    = Array.ofDim[Double](genMax)

        var pop = for(x <- 1 to popSize) yield p.potential()

        for(i <- 0 until genMax){
            pop = (1 to popSize by 2).flatMap { x=>
                val parentA = tournament(pop, tSize)(MinOrd)
                val parentB = tournament(pop, tSize)(MinOrd)
                val (childA, childB) = parentA.crossover(parentB)
                List(childA.mutate(), childB.mutate())
            }

            //log average population fitness
            averages(i) = pop.foldLeft(0.0)(_ + _.fitness) / pop.size.toDouble
            bests(i)    = pop.max(MinOrd).fitness
        }

        (averages, pop.max(MinOrd))
    }
    /**
     * Takes a random sampling of `num` elements from `xs` with replacement
     * Returns a new collection of the same type as `xs` whenever possible
     */
    def randomTake[T, CC[X] <: TraversableOnce[X]](xs: CC[T], num: Int)
      (implicit bf: CanBuildFrom[CC[T], T, CC[T]]): CC[T] = {
        //make array buffer for random access shuffling
        val buf = new ArrayBuffer[T] ++= xs
        val nb  = bf(xs)

        for(x <- 1 to num)
            nb += buf(GA.rand.nextInt(buf.size))

        nb.result()
    }

    class MinimizeOrd extends Ordering[Solution[_]] {
        def compare(a: Solution[_], b: Solution[_]) =
            b.fitness compare a.fitness
    }
    implicit object MinOrd extends MinimizeOrd

    def tournament[T <: Solution[T]](xs: TraversableOnce[T], num: Int)
      (implicit ord: Ordering[Solution[_]]): T = {
        val bracket = randomTake(xs, num)
        bracket.max(ord)
    }
}

object GA {
    private val rand = new Random()

}