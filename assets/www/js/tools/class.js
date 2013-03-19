(function() {
    var initializing = false, fnTest = /xyz/.test(function(){xyz;}) ? /\b_super\b/ : /.*/;
    // The base Class implementation (does nothing)
    this.ClassInternal = function(){};

    // Objects counter
    var counter = 0;
    
    // Create a new Class that inherits from this class
    ClassInternal.extend = function(prop) {
        var _super = this.prototype;
    
        // Instantiate a base class (but only create the instance,
        // don't run the init constructor)
        initializing = true;
        var prototype = new this();
        initializing = false;

        prototype.getId = function() { return this._id; };
        
        // Copy the properties over onto the new prototype
        for (var name in prop) {
            // Check if we're overwriting an existing function
            prototype[name] = prop[name];
            if (typeof prop[name] == "function") {
                prototype[name] = (function(name, fn) {
                    return function() {
                        var ret = null;
                        var args = Array.prototype.slice.apply(arguments);
                        args.unshift(this);

                        if (typeof _super[name] == "function" && fnTest.test(prop[name])) {
                            var tmp = this._super;
                          
                            // Add a new ._super() method that is the same method
                            // but on the super-class
                            this._super = _super[name];

                            // The method only need to be bound temporarily, so we
                            // remove it when we're done executing
                            ret = fn.apply(this, args);        
                            this._super = tmp;
                        } else {
                            ret = fn.apply(this, args);
                        }
                        return ret;
                    };
                })(name, prop[name]);
            }
        }
    
        // The dummy class constructor
        var ClassInternal = function () {
            this._id = ++counter;

            // All construction is actually done in the init method
            if (!initializing && this.init) {
                this.init.apply(this, arguments);
            }
        };
    
        // Populate our constructed prototype object
        ClassInternal.prototype = prototype;
    
        // Enforce the constructor to be what we expect
        ClassInternal.constructor = ClassInternal;
    
        // And make this class extendable
        ClassInternal.extend = arguments.callee;
    
        return ClassInternal;
    };
})();
