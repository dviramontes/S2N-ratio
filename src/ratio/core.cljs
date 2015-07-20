(ns ^:figwheel-always ratio.core
  (:require
    [quil.core :as q :include-macros true]
    [quil.middleware :as m]
    [reagent.core :as reagent :refer [atom]]))

(def WIDTH (.-innerWidth js/window))
(def HEIGHT (.-innerHeight js/window))

(def two-pi (* 2 (.-PI js/Math)))
(def x-spacing 16)
(def period 500)
(def amp 150)
(def dx (* x-spacing (/ two-pi period)))
(def y-values (range 0 (/ WIDTH x-spacing)))

(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text (str (.now js/Date))}))

(defn hello-world []
  [:h1 (:text @app-state)])

(reagent/render-component [hello-world]
                          (. js/document (getElementById "app")))
(defn on-js-reload []
  (print "reload....")
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 30)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :hsb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:color 0
   :angle 0
   :theta 0.0})

(defn update-state [state]
  ; Update sketch state by changing circle color and position.
  {:color (mod (+ (:color state) 0.7) 255)
   :angle (+ (:angle state) 0.1)
   :theta (+ (:theta state) 0.02)})

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  (q/no-stroke)
  ; Set circle color.
  (q/fill (:color state) 255 255)
  ; Calculate x and y coordinates of the circle.
  (let [angle (:angle state)
        x (* 100 (q/sin angle))
        y (* 100 (q/cos angle))
        d (/ WIDTH 20)
        values (map #(* (q/sin (:theta state)) amp) y-values)]
    ; Move origin point to the center of the sketch
    (q/with-translation [(/ (q/width) 2)
                         (/ (q/height) 2)]
                        ; Draw the circle.
                        (q/ellipse x y d d))
    (map (fn [x]
           (q/ellipse-mode :center)
           (q/ellipse (* x x-spacing) (+ (/ WIDTH 2) x) 16 16)) values)))

(q/defsketch hello-quil
             :host "canvas"
             :size [WIDTH HEIGHT]
             ; setup function called only once, during sketch initialization.
             :setup setup
             ; update-state is called on each iteration before draw-state.
             :update update-state
             :draw draw-state
             ; This sketch uses functional-mode middleware.
             ; Check quil wiki for more info about middlewares and particularly
             ; fun-mode.
             :middleware [m/fun-mode])