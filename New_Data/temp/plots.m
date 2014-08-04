function y = plots()
%plots the graphs for the 4 different states and 2 environments

%first load the data from the files (one file per state)
%each column represents a different action

%crossroads start
cs = importdata('gc2.txt', ',');
%ps = importdata('cs2ps.txt', ',');
%pa = importdata('cs2pa.txt', ',');
%pj = importdata('cs2pj.txt', ',');
%D = importdata('cs2D.txt', ',');

%grid start area center
%gc = importdata('gc2.txt', ',');

%grid start
%gs = importdata('gs.txt', ',');

%gr = importdata('gc2.txt', ',');

x = 0:1:(length(cs(:,1))-1)

%{
%plotting ps for state 2
figure;
plot(x, ps(:,1))
title('Crossroads (State 2): p_t(x)')
xlabel('Time')
ylabel('Probability')
grid on
print('-djpeg', 'ps.jpeg')

%plotting pa for each action
figure;
plot(pa)
legend('Up', 'Down', 'Left', 'Right', 'oDown', 'oUp')
title('Crossroads p(a)')
xlabel('Time')
ylabel('Probability')
grid on
print('-djpeg', 'pa.jpeg')


%plotting pj for state 2
figure;
plot(0:1:(length(pj(:,1))-1), pj(:,1))
title('Crossroads (State 2): p^j(x)')
xlabel('Time')
ylabel('Probability')
grid on
print('-djpeg', 'pj.jpeg')

%plotting D for state 2 and each action
figure;
plot(D)
legend('Up', 'Down', 'Left', 'Right', 'oDown', 'oUp')
title('Crossroads (state 2): D')
xlabel('Time')
ylabel('D value')
grid on
print('-djpeg', 'D.jpeg')
%}


%for crossroads

%up, down, left, right, odown, oup

%{
figure;
plot(x, cs(:,1))
title('Crossroads (State 2): Up')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,2))
title('Crossroads (State 2): Down')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,3))
title('Crossroads (State 2): Left')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,4))
title('Crossroads (State 2): Right')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,5))
title('Crossroads (State 2): oDown')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, cs(:,6))
title('Crossroads (State 2): oUp')
xlabel('Trial')
ylabel('Probability')
grid on
%}

%combined graph for all actions
figure;
plot(cs)
legend('Up', 'Down', 'Left', 'Right', 'o3', 'oRight', 'oDown')
title('Crossroads (State 11): Policy over time')
xlabel('Trial')
ylabel('Probability')
grid on
print('-djpeg', 'cs.jpeg')

%{
%for grid
figure;
plot(x, gr(:,1))
title('Grid (State 11): Up')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,2))
title('Grid (State 11): Down')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,3))
title('Grid (State 11): Left')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,4))
title('Grid (State 11): Right')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,5))
title('Grid (State 11): oDown')
xlabel('Trial')
ylabel('Probability')
grid on

figure;
plot(x, gr(:,6))
title('Grid (State 11): oRight')
xlabel('Trial')
ylabel('Probability')
grid on

%combined graph for all actions
figure;
plot(gr)
legend('Up', 'Down', 'Left', 'Right', 'oDown', 'oRight')
title('Grid (State 11)')
xlabel('Trial')
ylabel('Probability')
grid on
%}
