
#!/usr/bin/gnuplot

reset
set term png size 1024, 768
set ylabel "MFLOP/s"
set xlabel "time (sec)"
#set xrange [1:20]
set title "Achieved FLOPS Over Time"
set grid
#set logscale
#set yrange [1:175]
set format x "%1.1f"
set out 'public/profile/24e1da68-04a9-4d2d-820e-f029afb49375/timeline.png'
plot "public/profile/24e1da68-04a9-4d2d-820e-f029afb49375/timeline.data" using 2:3 with line title "MFLOPS"
      