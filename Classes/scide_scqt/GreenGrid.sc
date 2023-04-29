//TODO set skin

GreenGrid : SCViewHolder {

	var <>action;  //Function
	var <array;  //Array with Int8Arrays
	var <cols;  //Integer
	var <rows;  //Integer

	var >clickArea= 0.8;  //sensitivity when clicking
	var >dragArea= 0.6;  //sensitivity when dragging

	var <gridWidth= 1;
	var <drawMethod= \addRect;

	var usr, bg;

	*new {|parent, bounds, cols= 10, rows= 12, action|
		^super.new.initGreenGrid(parent, bounds, cols, rows, action)
	}

	initGreenGrid {|parent, bounds, argCols, argRows, argAction|

		var lastCol= -1, lastRow= -1;
		var skin= GUI.skins.guiCV ?? {(
			background: Color.black,
			foreground: Color.green(0.667),
			highlight: Color.green,
			margin: Point(8, 8),
		)};

		cols= argCols;
		rows= argRows;
		action= argAction;

		view= View(parent, bounds).layout_(
			StackLayout(
				usr= UserView(),
				bg= UserView()
			).mode_(\stackAll)
		).minSize_(Size(150, 150));

		array= {Int8Array.newClear(cols)}!rows;

		bg.background_(skin.background);
		if(gridWidth>0, {
			bg.drawFunc_{|usr|
				var cellWidth= usr.bounds.width/cols;
				var cellHeight= usr.bounds.height/rows;
				Pen.smoothing_(false);
				Pen.width_(gridWidth);
				Pen.strokeColor_(skin.foreground);
				Pen.translate(-0.5, -0.5);
				(cols+1).do{|x|
					Pen.moveTo(Point(x*cellWidth, 0));
					Pen.lineTo(Point(x*cellWidth, usr.bounds.height));
				};
				(rows+1).do{|y|
					Pen.moveTo(Point(0, y*cellHeight));
					Pen.lineTo(Point(usr.bounds.width, y*cellHeight));
				};
				Pen.stroke;
			};
		});

		usr.background_(Color.clear);
		usr.drawFunc= {|usr|
			var border= skin.margin;
			var cellWidth= usr.bounds.width/cols;
			var cellHeight= usr.bounds.height/rows;
			var cwb= cellWidth-border.x;
			var chb= cellHeight-border.y;
			var bx2= border.x/2;
			var by2= border.y/2;
			Pen.fillColor_(skin.highlight);
			array.do{|col, y|
				col.do{|c, x|
					if(c!=0, {
						Pen.perform(drawMethod, Rect(cellWidth*x+bx2, cellHeight*y+by2, cwb, chb));
					});
				};
			};
			Pen.fill;
			//array.postln;  //debug
		};

		usr.mouseDownAction_{|v, x, y|
			var cellCol= (x/v.bounds.width*cols).asInteger;
			var cellRow= (y/v.bounds.height*rows).asInteger;
			this.prCheckAndSet(v, x, y, cellCol, cellRow, clickArea*0.5);
			lastCol= cellCol;
			lastRow= cellRow;
		};
		usr.mouseMoveAction_{|v, x, y|
			var cellCol= ((x/v.bounds.width).clip(0, 1)*cols).asInteger;
			var cellRow= ((y/v.bounds.height).clip(0, 1)*rows).asInteger;
			if(lastCol!=cellCol or:{lastRow!=cellRow}, {
				if(this.prCheckAndSet(v, x, y, cellCol, cellRow, dragArea*0.5), {
					lastCol= cellCol;
					lastRow= cellRow;
				});
			});
		};

		if(bounds.isNil, {
			view
			.name_(this.class.name)
			.front;
		});
	}

	array_ {|arr|
		if(arr.size==rows and:{arr.every{|a| a.size==cols and:{a.isKindOf(Int8Array)}}}, {
			array= arr;
			usr.refresh;
		}, {
			"%: argument not an Array of Int8Arrays (%x%).".format(this.class, rows, cols).warn;
		});
	}

	fill {|func|
		array= {|col|
			Int8Array.newFrom({|row| func.value(col, row)}.dup(cols));
		}.dup(rows);
		usr.refresh;
	}

	cols_ {|num|
		cols= num.max(1).asInteger;
		this.clear;
		bg.refresh;
	}
	rows_ {|num|
		rows= num.max(1).asInteger;
		this.clear;
		bg.refresh;
	}

	at {|col, row|  //x, y
		^array[row][col]
	}
	colAt {|col|
		^array.collect{|row| row[col]}
	}
	rowAt {|row|
		^array[row]
	}

	setCol {|col, arr|
		col= col.clip(0, cols-1);
		arr= arr.wrapExtend(rows).collect{|a| if(a.isNil or:{a==0}, 0, 1)};
		array.do{|a, i| a[col]= arr[i]};
		usr.refresh;
	}
	setRow {|row, arr|
		row= row.clip(0, rows-1);
		arr= arr.wrapExtend(cols).collect{|a| if(a.isNil or:{a==0}, 0, 1)};
		array[row]= Int8Array.newFrom(arr);
		usr.refresh;
	}

	setAt {|col, row|
		array[row][col]= 1;
		usr.refresh;
	}
	clearAt {|col, row|
		array[row][col]= 0;
		usr.refresh;
	}

	clear {
		array= array.collect{|cols| cols= Int8Array.newClear(cols.size)};
		usr.refresh;
	}

	//--look&feel

	drawMethod_ {|name|
		if(name==\addRect or:{name==\addOval}, {
			drawMethod= name;
			usr.refresh;
		}, {
			"%: method '%' not available. Use addRect or addOval.".format(this.class, name).warn;
		});
	}
	gridWidth_ {|val|
		gridWidth= val;
		bg.refresh;
	}

	//--private

	prCheckAndSet {|v, x, y, cellCol, cellRow, dist|
		var cellWidth= v.bounds.width/cols;
		var cellHeight= v.bounds.height/rows;
		var cellCenterX= cellCol*cellWidth+(cellWidth*0.5);
		var cellCenterY= cellRow*cellHeight+(cellHeight*0.5);
		^if((cellCenterX-x).abs<(cellWidth*dist) and:{(cellCenterY-y).abs<(cellHeight*dist)}, {
			array[cellRow][cellCol]= 1-array[cellRow][cellCol];
			action.value(cellCol, cellRow, array[cellRow][cellCol]);
			v.refresh;
			true
		}, {
			false
		})
	}
}

/*
g= GreenGrid();
g.array= {Int8Array.newFrom({2.rand}!g.cols)}!g.rows;
g.action= {|x,y,v| [x,y,v].postln}
g.at(1,2)
g.setAt(1,2)
g.setRow(2, Int8Array[0,0,1,0,0,1,0,0,1,0])
g.setCol(0, [1, 1, 1])
g.setRow(3, [0, \tt,1])
g.array[2]
g.rowAt(1)
g.colAt(1)
g.clear
g.at(9,10)
g.cols=7
g.cols
g.rows= 1
g.gridWidth= 2
g.drawMethod= \addOval
g.clear
g.array.size
g.at(1,0)
g.rowAt(1)
g.colAt(9)
g.array.collect{|x| x[2]}
*/