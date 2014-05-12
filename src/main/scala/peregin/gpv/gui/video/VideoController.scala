package peregin.gpv.gui.video

import com.xuggle.mediatool.MediaToolAdapter
import com.xuggle.mediatool.event.IVideoPictureEvent
import peregin.gpv.util.Logging


class VideoController(timeHandler: (Long, Int) => Unit, durationInMillis: Long) extends MediaToolAdapter with Logging {
  
  var firstVideoTs: Option[Long] = None
  var firstClockTs = 0L
  var sleep = 0L

  override def onVideoPicture(event: IVideoPictureEvent) = {
    val tsInMillis = event.getTimeUnit.toMillis(event.getTimeStamp)
    val percentage = if (durationInMillis > 0) tsInMillis * 100 / durationInMillis else 0
    timeHandler(tsInMillis, percentage.toInt)
    if (firstVideoTs.isEmpty) {
      firstVideoTs = Some(tsInMillis) // micro
      firstClockTs = System.currentTimeMillis()
      sleep = 0
    } else {
      val now = System.currentTimeMillis()
      val elapsedClock = now - firstClockTs
      val elapsedVideo = tsInMillis - firstVideoTs.get
      sleep = elapsedVideo - elapsedClock
    }

    if (sleep > 0) {
      //log.info(s"sleep = $sleep millis")
      Thread.sleep(sleep)
    }

    super.onVideoPicture(event)
  }
}
