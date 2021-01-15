ECHO OFF

SET CP=java -classpath "target\ffmpeg-utils-1.0.0.jar"

FOR %%F IN (target\lib\*.jar) DO call :addcp %%F
goto extlibe

:addcp
SET CP=%CP%;"%1"
goto :eof

:extlibe
SET CP=%CP% com.gdxsoft.ffmpegUtils.test
%CP%