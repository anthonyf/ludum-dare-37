(ns ld37.common)

(def title "Catalingus")

(def screen-size [1024 768])

(def tile-width 64)

(def game-speed 0.25)

(defn game-to-screen-pos
  [actor [x y]]
  [(+ (- (* tile-width x)
         (/ (.getWidth actor) 2))
      (/ tile-width 2))
   (+ (- (* tile-width y)
         (/ (.getHeight actor) 2))
      (/ tile-width 2))])


(defn set-actor-game-position
  [actor game-pos]
  (let [[x y] (game-to-screen-pos actor game-pos)]
    (.setPosition actor x y)))
