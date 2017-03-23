<?php
$a =0;
	$TIME="/ramdisk/ngwt/time";
	$PID="/ramdisk/ngwt/pid";

while($a == 0){


	if(!file_exists($TIME)){
 		echo 'time file no';
	}elseif(!file_exists($PID)){
		echo 'pid file no';
	}elseif (time() - file_get_contents($TIME) >= 120){
		echo 'ill killed '.$pi;
		if(is_numeric($pi)){
			system("kill ".$pi);
		}else{
			echo 'wtf '.$pi;
		}
	}else{
		echo((time() - file_get_contents($TIME))."    "	);
	}
	sleep(60);

}


?>
