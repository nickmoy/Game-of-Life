a.out:
	javac Game_of_Life.java
	@echo ">> Project compiled!"

clean:
	# [ -f Game_of_Life.class ] && rm Game_of_Life.class  || true
	# [ -f Game_of_Life$$1.class ] && rm Game_of_Life$$1.class || true
	@myarray=(`find ./ -maxdepth 1 -name "*.class"`) ; \
		if [ $${#myarray[@]} -gt 0 ]; then \
			rm *.class; \
		fi
	@echo ">> Project cleaned!"
