import "./FilterBar.css";

function FilterBar() {

    return (

        <div className="filter-bar">

            <select>

                <option>Todas</option>

                <option>Perros</option>

                <option>Gatos</option>

            </select>

            <select>

                <option>Sexo</option>

                <option>Macho</option>

                <option>Hembra</option>

            </select>

        </div>

    );

}

export default FilterBar;