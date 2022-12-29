//TODO option to survive CmdPeriod
//TODO format output a bit better?
//TODO alarm
//TODO GreenTimerGUI

GreenTimer {

	var <startTime;

	*new {|post= false, interval= 1|
		^super.new.initGreenTimer(post, interval);
	}

	initGreenTimer {|post, interval|
		startTime= SystemClock.seconds;
		if(post, {
			SystemClock.sched(0, {
				this.currentTime.postln;
				interval;
			});
		});
	}

	currentTime {
		^SystemClock.seconds-startTime;
	}
}
