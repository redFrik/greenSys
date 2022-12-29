GreenScope : SCViewHolder {

	*new {|parent, bounds, index= 0|
		^super.new.initGreenScope(parent, bounds, index)
	}

	initGreenScope {|parent, bounds, index|

		var skin= GUI.skins.guiCV;
		var scopeView, userView;

		var bus= Bus(\audio, index, 2);
		var synth= BusScopeSynth();
		synth.play(2048, bus, 1024);

		scopeView= ScopeView(parent, bounds)
		.bufnum_(synth.bufferIndex)
		.server_(Server.default)
		.style_(2)
		.waveColors_(skin.highlight!2)
		.yZoom_(0.5.sqrt)
		.start;

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

		scopeView.onClose_({bus.free; synth.free});
		CmdPeriod.doOnce({this.close});

		view.front
	}
}
