$(function() {
	$('table').on('click', '.delete_user', function(event) {
		xoa = $(this).parent().parent();
		var parentOjb = $(this).closest('.rename');
		id = parentOjb.find('.user_id').text();
		$.ajax({
			url : 'users/'+id,
			type : 'DELETE',
			success : function(data) {
				xoa.remove();
				alert(data);
			},
			error : function(e) {
				alert("User have data referenc in onther table can't delete!");
				console.log(e);
			}
		})
	});

	$('table').on('click', '.edit_user', function(event) {
		var parentOjb = $(this).closest('.rename');
		var id = parentOjb.find('.user_id').text();
		$('#myModal').modal('show');
		$.ajax({
			url : 'users/'+id,
			type : 'GET',
			success : function(data) {
				$('#cuser_id').val(data.id);
				$('#cname').val(data.name);
				$("#cemail").val(data.email);
				$('#crole').val(data.role.id);
			},
			error : function(e) {
				console.log(e);
			}
		})
		
	});
	// Update User
	$("#save_user").click(function() {
		var user = $('form').getForm2obj();
		console.log(user);
		$.ajax({
			type: "POST",
		      contentType : 'application/json; charset=utf-8',
		      dataType : 'json',
		      url: "users",
		      data: JSON.stringify(user),
			success : function(data) {
				alert(data);
			},
			error : function(e) {
				alert(e.responseText);
			}
		})
		$('#myModal').modal('hide');
		$('#reload').load(location.href + " #reload>*");
	});
	//Get json object from form
	(function($){
	    $.fn.getForm2obj = function(){
	        var _ = {},_t=this;
	        this.c = function(k,v){ eval("c = typeof "+k+";"); if(c == 'undefined') _t.b(k,v);}
	        this.b = function(k,v,a = 0){ if(a) eval(k+".push("+v+");"); else eval(k+"="+v+";"); };
	        $.map(this.serializeArray(),function(n){
	            if(n.name.indexOf('[') > -1 ){
	                var keys = n.name.match(/[a-zA-Z0-9_]+|(?=\[\])/g),le = Object.keys(keys).length,tmp = '_';
	                $.map(keys,function(key,i){
	                    if(key == ''){
	                        eval("ale = Object.keys("+tmp+").length;");
	                        if(!ale) _t.b(tmp,'[]');
	                        if(le == (i+1)) _t.b(tmp,"'"+n['value']+"'",1);
	                        else _t.b(tmp += "["+ale+"]",'{}');
	                    }else{
	                        _t.c(tmp += "['"+key+"']",'{}');
	                        if(le == (i+1)) _t.b(tmp,"'"+n['value']+"'");
	                    }
	                });
	            }else _t.b("_['"+n['name']+"']","'"+n['value']+"'");
	        });
	        return _;
	    }
	})(jQuery);
	
});