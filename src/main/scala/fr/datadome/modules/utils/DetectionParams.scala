package fr.datadome.modules.utils

import java.time.OffsetDateTime

/**
  * @param avgDurationOnPage la moyenne minimal en secondes entre deux logs successifs pour la fonction DetectionMethods.avgDurationBetweenCalls
  * @param start Date de début d'analyse pour la fonction DetectionMethods.parallelCalls
  * @param end Date de fin d'analyse  pour la fonction DetectionMethods.parallelCalls
  * @param parallelFrequencySeconds nombre de second à analyser pour la fonction DetectionMethods.parallelCalls
  * @param parallelTimes nombre d'appels authorisés dans la  parallelFrequencySeconds pour la fonction DetectionMethods.parallelCalls
  * @param loopingTolerated nombre d'appels successif authorisé pour la fonction DetectionMethods.theSamePageIsCalledSuccessifly
  */
case class DetectionParams(
    avgDurationOnPage: Float,
    start: OffsetDateTime,
    end: OffsetDateTime,
    parallelFrequencySeconds: Long,
    parallelTimes: Int,
    loopingTolerated: Long
)
