GreenScope : SCViewHolder {

	var scopeView, bus, synth, <isPlaying;
	var userView;

	*new {|parent, bounds, server, index= 0|
		^super.new.initGreenScope(parent, bounds, server, index)
	}

	initGreenScope {|parent, bounds, server, index|

		var skin= GUI.skins.guiCV;

		server= server ? Server.default;
		bus= Bus(\audio, index, 2, server);

		scopeView= ScopeView(parent, bounds)
		.server_(server)
		.style_(2)
		.waveColors_(skin.highlight!2)
		.yZoom_(0.5.sqrt);

		userView= UserView(parent, bounds)
		.acceptsMouse_(false)
		.canFocus_(false)
		.drawFunc_({|usr|
			var w= usr.bounds.width*0.5;
			var h= usr.bounds.height*0.5;
			var d= w.min(h)*scopeView.yZoom;
			var d1= d*0.5.sqrt;
			var d2= d*2.sqrt;
			var d3= d*0.1;
			Pen.translate(w, h);
			Pen.strokeColor= skin.foreground;
			Pen.addOval(Rect.aboutPoint(Point(0, 0), d1, d1));
			Pen.addOval(Rect.aboutPoint(Point(0, 0), d2, d2));
			Pen.moveTo(Point(0-d3, 0));
			Pen.lineTo(Point(d3, 0));
			Pen.moveTo(Point(0, 0-d3));
			Pen.lineTo(Point(0, d3));
			Pen.stroke;
		});

		view= View(parent, bounds).layout_(
			StackLayout(userView, scopeView).mode_(\stackAll)
		)
		.background_(skin.background)
		.minSize_(Size(150, 150));

		if(bounds.isNil, {
			view
			.name_(this.class.name)
			.front;
		});

		ServerQuit.add(this, server);
		ServerTree.add(this, server);
		if(server.serverRunning, {
			this.doOnServerTree(server);
		});

		scopeView.onClose_({
			scopeView.stop;
			isPlaying= false;
			synth.free;
			bus.free;
			ServerQuit.remove(this, server);
			ServerTree.remove(this, server);
		});

		view.keyDownAction_({|view, chr|
			switch(chr,
				Char.space, {
					if(isPlaying, {
						scopeView.stop;
						isPlaying= false;
					}, {
						scopeView.start;
						isPlaying= true;
					})
				},
				$+, {
					scopeView.yZoom_(scopeView.yZoom+0.1);
					userView.refresh;
				},
				$-, {
					scopeView.yZoom_((scopeView.yZoom-0.1).max(0));
					userView.refresh;
				},
				$=, {
					scopeView.yZoom_(0.5.sqrt);
					userView.refresh;
				},
			)
		});
	}

	doOnServerTree {|server|
		synth= BusScopeSynth(server);
		synth.play(2048, bus, 1024);  //TODO experiment with these
		scopeView
		.bufnum_(synth.bufferIndex)
		.start;
		isPlaying= true;
	}

	doOnServerQuit {|server|
		scopeView.stop;
		isPlaying= false;
	}

	start {
		if(isPlaying.not, {
			scopeView.start;
			isPlaying= true;
		})
	}

	stop {
		if(isPlaying, {
			scopeView.stop;
			isPlaying= false;
		})
	}

	zoom_ {|factor= 0.707|
		scopeView.yZoom= factor.max(0);
		userView.refresh;
	}
	zoom {^scopeView.yZoom}
}
