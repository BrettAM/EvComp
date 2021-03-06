package com.RealSeqFunction

import com.util.Entropy.rand

trait RSMutate {
    def apply(dna: Seq[Float], min: Float, max: Float): Seq[Float]
}

class GaussMutate(sdv: Float) extends RSMutate{
    def apply(dna: Seq[Float], min: Float, max: Float): Seq[Float] = {
        def m(v: Float): Float = {
            val tmp = v + ((rand nextGaussian).toFloat * sdv)
            Math.min(max, Math.max(tmp, min)).toFloat
        }
        dna.map(m(_))
    }
}

class SelectiveMutate(mutateChance: Float, sdv: Float) extends RSMutate{
    def apply(dna: Seq[Float], min: Float, max: Float): Seq[Float] = {
        def m(v: Float): Float = {
            if(rand.nextFloat < mutateChance){
                val tmp = v + ((rand nextGaussian).toFloat * sdv)
                Math.min(max, Math.max(tmp, min)).toFloat
            } else {
                v
            }
        }
        dna.map(m(_))
    }
}
