GreenScope : SCViewHolder {

	var scopeView, bus, synth;

	*new {|parent, bounds, server, index= 0|
		^super.new.initGreenScope(parent, bounds, server, index)
	}

	initGreenScope {|parent, bounds, server, index|

		var skin= GUI.skins.guiCV;
		var userView;

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
			var d= w.min(h);
			Pen.translate(w, h);
			Pen.strokeColor= skin.foreground;
			Pen.addOval(Rect.aboutPoint(Point(0, 0), d*0.5, d*0.5));
			Pen.addOval(Rect.aboutPoint(Point(0, 0), d, d));
			Pen.moveTo(Point(0-d*0.1, 0));
			Pen.lineTo(Point(d*0.1, 0));
			Pen.moveTo(Point(0, 0-d*0.1));
			Pen.lineTo(Point(0, d*0.1));
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
			this.doOnServerTree;
		});

		scopeView.onClose_({
			scopeView.stop;
			synth.free;
			bus.free;
			ServerQuit.remove(this, server);
			ServerTree.remove(this, server);
		});
	}

	doOnServerTree {|server|
		synth= BusScopeSynth(server);
		synth.play(2048, bus, 1024);  //TODO experiment with these
		scopeView
		.bufnum_(synth.bufferIndex)
		.start;
	}

	doOnServerQuit {|server|
		scopeView.stop;
	}
}
