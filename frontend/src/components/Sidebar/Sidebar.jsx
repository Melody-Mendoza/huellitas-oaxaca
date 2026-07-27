import "./Sidebar.css";

function Sidebar(){
    return(
        <aside className="sidebar">
            <h3>Filtros</h3>
            <div className="filter-group">
                <h4>Especie</h4>
                <label><input type="checkbox"/> Perros (42)</label>
                <label><input type="checkbox"/> Gatos (18)</label>
                <label><input type="checkbox"/> Otros (3)</label>
            </div>

            <div className="filter-group">
                <h4>Edad</h4>
                <label><input type="checkbox"/> Cachorro</label>
                <label><input type="checkbox"/> Joven</label>
                <label><input type="checkbox"/> Adulto</label>
                <label><input type="checkbox"/> Senior</label>

            </div>

            <div className="filter-group">
                <h4>Tamaño</h4>
                <div className="chips">
                    <button>Pequeño</button>
                    <button className="active">Mediano</button>
                    <button>Grande</button>
                </div>

            </div>
            <div className="filter-group">
                <h4>Sexo</h4>
                <label><input type="radio" name="sexo"/> Macho</label>
                <label><input type="radio" name="sexo"/> Hembra</label>
                <label><input type="radio" name="sexo"/> Ambos</label>

            </div>

        </aside>

    );

}

export default Sidebar;